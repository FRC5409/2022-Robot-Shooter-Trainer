from time import time
from matplotlib import patches
from model import DataModel

import matplotlib.ticker as tck
import numpy as np

import utility

class ModelSubplot:
    X1 = np.linspace(0,1,100)
    X2 = X1*X1

    def __init__(self, parent, name, layout, domain, range, points, linecolor, pointcolor, radius=0.1):
        assert len(points) == 3

        self.parent = parent
        self.points = []
        self.nodes = []
        self.model = DataModel.from_points(points)

        self.plot = parent.fig.add_subplot(layout)
        self.plot.set_title(name)

        self.plot.xaxis.set_major_formatter(
            tck.FuncFormatter(lambda x, pos: '{:.0f}'.format(x*(domain[0]-domain[1]) + domain[1]))
        )

        self.plot.yaxis.set_major_formatter(
            tck.FuncFormatter(lambda x, pos: '{:.0f}'.format(x*(range[0]-range[1]) + range[1]))
        )

        self.plot.autoscale(False)
        
        nrows = utility.get_digit(layout, 1)
        ncols = utility.get_digit(layout, 2)

        aspect_radius = utility.get_aspect(ncols, nrows, radius)
        
        self.line, = self.plot.plot(
            ModelSubplot.X1, self.model.apply(ModelSubplot.X1, ModelSubplot.X2), color=linecolor, zorder=0)

        for position in points:
            self.points.append(position)
            self.nodes.append(DraggablePoint(self, position, pointcolor, aspect_radius))


    def update(self):
        self.points = [x.get_point() for x in self.nodes]
        self.model = DataModel.from_points(self.points)
        self.line.set_ydata(self.model.apply(ModelSubplot.X1, ModelSubplot.X2))
    
    def draw(self):
        self.plot.draw_artist(self.line)

    def set_active(self, active):
        self.parent.set_active(active)

    def set_animated(self, value):
        self.line.set_animated(value)

    def get_points(self):
        return self.points

    def get_model(self):
        return self.model

# https://stackoverflow.com/questions/28001655/draggable-line-with-draggable-points
class DraggablePoint:
    lock = None

    def get_aspect(fig, r):
        x_lim = fig.get_xlim()
        y_lim = fig.get_ylim()

        f = utility.get_aspect(abs(x_lim[0]-x_lim[1]), abs(y_lim[0]-y_lim[1]))
        return (r[0]*f[1], r[1]*f[0])

    def __init__(self, parent, position, color, radius=(0.1,0.1)):
        self.parent = parent
        self.color = color
        self.x = position[0]
        self.y = position[1]
        self.background = None
        self.offset = None
        self.radius = DraggablePoint.get_aspect(parent.plot, radius)
        self.patch = patches.Ellipse(position, self.radius[0], self.radius[1], fc=color, zorder=1)

        parent.plot.add_patch(self.patch)

        fig = parent.parent.fig
        self.update_aspect(fig.get_window_extent().transformed(fig.dpi_scale_trans.inverted()), False)
        
        self.connect() 

    def connect(self):
        canvas = self.patch.figure.canvas

        self.event_onpress = canvas.mpl_connect('button_press_event', self.on_press)
        self.event_onmotion = canvas.mpl_connect('motion_notify_event', self.on_motion)
        self.event_onrelease = canvas.mpl_connect('button_release_event', self.on_release)
        self.event_onresize = canvas.mpl_connect('resize_event', self.on_resize)

    def disconnect(self):
        canvas = self.patch.figure.canvas

        canvas.mpl_disconnect(self.event_onpress)
        canvas.mpl_disconnect(self.event_onmotion)
        canvas.mpl_disconnect(self.event_onrelease)
        canvas.mpl_disconnect(self.event_onresize)

    def on_press(self, event):
        if event.inaxes != self.patch.axes: return
        if DraggablePoint.lock is not None: return

        contains, _ = self.patch.contains(event)
        if not contains: return
        
        # obtain point lock (only one point can be animated at a time)
        DraggablePoint.lock = self
        self.patch.set_animated(True)
        self.parent.set_animated(True)
        self.parent.set_active(True)

        canvas = self.patch.figure.canvas
        axes = self.patch.axes
        
        self.offset = (self.x - event.xdata, self.y - event.ydata)

        canvas.draw()
        self.background = canvas.copy_from_bbox(self.patch.axes.bbox)

        self.parent.draw()

        axes.draw_artist(self.patch)
        
        # and blit just the redrawn area
        canvas.blit(axes.bbox)
    
    def on_motion(self, event):
        if DraggablePoint.lock is not self: return
        if event.inaxes != self.patch.axes: return

        canvas = self.patch.figure.canvas
        axes = self.patch.axes

        self.update_position(event)
        self.parent.update()

        canvas.restore_region(self.background)
    
        self.parent.draw()
        
        axes.draw_artist(self.patch)

        canvas.blit(axes.bbox)
  
    def on_release(self, event):
        if DraggablePoint.lock is not self: return
        # Release acquired lock
        DraggablePoint.lock = None
        
        self.patch.set_animated(False)
        self.parent.set_animated(False)

        self.update_position(event)
        self.parent.update()

        self.background = None

        # redraw the full figure
        self.patch.figure.canvas.draw()
        self.parent.set_active(False)

    def on_resize(self, event):
        self.update_aspect(event, True)

    def update_aspect(self, wh, redraw):
        aspect = utility.get_aspect(wh.width, wh.height)
        
        self.patch.set_width(self.radius[0] * aspect[0])
        self.patch.set_height(self.radius[1] * aspect[1])

        if redraw:
            self.patch.axes.draw_artist(self.patch)
    
    def update_position(self, event):
        self.x = self.offset[0] + event.xdata
        self.y = self.offset[1] + event.ydata

        self.patch.center = (self.x, self.y)

    def get_point(self):
        return (self.x, self.y)

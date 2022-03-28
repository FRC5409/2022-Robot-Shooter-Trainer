from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas
from matplotlib import pyplot as plt
from matplotlib.figure import Figure

from options import ApplicationOptions
from graphics import DraggablePoint, ModelSubplot

from PyQt5 import QtWidgets, QtGui

import numpy as np

import matplotlib
import utility

class ModelEditorApplication(FigureCanvas):
    def __init__(self, options: ApplicationOptions):
        matplotlib.use(options.backend)
    
        self.fig = Figure()

        self.plots = []

        FigureCanvas.__init__(self, self.fig)

        FigureCanvas.setSizePolicy(self, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Expanding)
        FigureCanvas.updateGeometry(self)

        self.setParent(None)
        self.show()

        self.plots.append(
            ModelSubplot(self, "Turret Offset", 211,
                (-2,2), (-10, 10), [(0.2,0.5), (0.4,0.5), (0.6,0.5)], utility.createRandomColor(0), utility.createRandomColor(0), 0.05)
        )

        self.plots.append(
            ModelSubplot(self, "Flywheel Offset", 212,
                (-2,2), (-10, 10), [(0,0), (1,0), (0,1)], utility.createRandomColor(0), utility.createRandomColor(0), 0.05)
        )

        self.updateFigure()

    def clearFigure(self):
        self.axes.clear()
        self.axes.grid(True)

        del(self.list_points[:])

        self.updateFigure()


    def updateFigure(self):
        self.draw()


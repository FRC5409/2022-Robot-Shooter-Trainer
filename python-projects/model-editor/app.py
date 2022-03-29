import genericpath
import os
import re
from time import time
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas
from matplotlib import pyplot as plt
from matplotlib.figure import Figure

from options import ApplicationOptions, ModelOptions
from graphics import ModelSubplot
from storage import DataStorage, OptionsStorage

from PyQt5 import QtWidgets, QtCore, QtGui

import numpy as np

import matplotlib
import utility

class ModelConfiguration:
    def __init__(self, name, storage, plot, options):
        self.name = name
        self.plot = plot
        self.options = options
        self.storage = storage

    def get_points(self):
        return self.plot.get_points()

    def get_model(self):
        return self.plot.get_model()

class ModelEditorApplication(FigureCanvas):
    def __init__(self, options: ApplicationOptions):
        matplotlib.use(options.backend)

        self.fig = Figure()
        
        FigureCanvas.__init__(self, self.fig)
        FigureCanvas.setSizePolicy(self, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Expanding)
        FigureCanvas.updateGeometry(self)

        self.options = options
        
        self.plots = []
        self.active = False
        self.saved = True

        self.setParent(None)
        self.show()

        self.save_timer = QtCore.QTimer(self)
        self.save_timer.setSingleShot(True)
        self.save_timer.timeout.connect(self.save)

        self.configurations = self.get_configurations()

        self.draw()
        self.log()

    def closeEvent(self, event: QtGui.QCloseEvent) -> None:
        self.save()
        
    def save(self):
        for config in self.configurations.values():
            config.storage.write(config.get_points())

        self.log()
        self.setWindowTitle("Saved")
        self.saved = True

    def log(self):
        for config in self.configurations.values():
            model = config.get_model()
            print(config.name, model.kA, model.kB, model.kC, sep=", ")

    def set_active(self, active):
        if active:
            if self.active: return

            if self.save_timer.isActive():
                self.save_timer.stop()

            self.active = True
        else:
            if not self.active: return

            self.save_timer.start(int(self.options.update_time*10000))

            self.active = False
        
        self.saved = False
        self.setWindowTitle("Saving...")

    def get_configurations(self):
        configurations = dict()

        if not os.path.exists(self.options.environment):
            os.makedirs(self.options.environment)
            return configurations

        names = []
        for name in os.listdir(self.options.environment):
            if genericpath.isfile(name) or not re.match(r"^[a-z\_\-0-9A-Z ]+$", name):
                continue
            names.append(name)

        size = len(names)

        layout = None
        scale = None
        if (size < 2):
            layout = 111
            scale = 1
        elif (size > 2):
            layout = 221
            scale = 2
        else:
            layout = 211
            scale = 2

        for i, name in enumerate(names):
            cdir = os.path.join(self.options.environment, name)

            options_storage = OptionsStorage(os.path.join(cdir, self.options.options_name))
            options = ModelOptions(options_storage.read())

            model_storage = DataStorage(os.path.join(cdir, self.options.model_name))
            points = model_storage.read()

            color = utility.createColorPair(0.8)
            
            plot = ModelSubplot(self, name, layout + i,
                (options.min_domain, options.max_domain),
                (options.min_range, options.max_range),
                points, color[0], color[1], self.options.point_radius * scale
            )

            configurations[name] = ModelConfiguration(name, model_storage, plot, options)

        return configurations



    


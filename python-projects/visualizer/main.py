from genericpath import isfile
import os
import random
import sys

import matplotlib
import matplotlib.pyplot as plt
import matplotlib.ticker as tck

import numpy as np

from typing import Dict, Final
from storage import DataStorage, ModelStorage, ConfigurationStorage

DEFAULT_MODEL_STORAGE_FILE: Final = "model.csv"
DEFAULT_DATA_STORAGE_FILE: Final  = "data.csv"
DEFAULT_ENVIRONMENT: Final        = ".temp/training/configurations"
DEFAULT_MAX_SPEED: Final          = "1000"
DEFAULT_MIN_SPEED: Final          = "0"
DEFAULT_MAX_DISTANCE: Final       = "10"
DEFAULT_MIN_DISTANCE: Final       = "0"
DEFAULT_BACKEND: Final            = "QtAgg"
DEFAULT_SHADE: Final              = "0.8"

def main(args):
    # Get arguments
    model_name   = args.get("model_name", DEFAULT_MODEL_STORAGE_FILE)
    data_name    = args.get("data_name",  DEFAULT_DATA_STORAGE_FILE)
    environment  = args.get("environment",  DEFAULT_ENVIRONMENT)
    color_shade  = float(args.get("shading", DEFAULT_SHADE))
    max_speed    = float(args.get("max_speed", DEFAULT_MAX_SPEED))
    min_speed    = float(args.get("min_speed", DEFAULT_MIN_SPEED))
    max_distance = float(args.get("max_distance", DEFAULT_MAX_DISTANCE))
    min_distance = float(args.get("min_distance", DEFAULT_MIN_DISTANCE))

    matplotlib.use(args.get("backend", DEFAULT_BACKEND))

    if max_speed > min_speed:
        temp = max_speed
        max_speed = min_speed
        min_speed = temp

    if max_distance > min_distance:
        temp = max_distance
        max_distance = min_distance
        min_distance = temp

    # create domain space
    x = np.linspace(0, 1, 100)
    y = np.linspace(0, 1, 100)

    plt.ion()
    
    fig = plt.figure()
    ax = fig.add_subplot(111)

    plt.xlabel("Distance (ft)")
    plt.ylabel("Speed (rpm)")

    ax.xaxis.set_major_formatter(
        tck.FuncFormatter(lambda x, pos: '{:.0f}'.format(x*(min_distance-max_distance)))
    )

    ax.yaxis.set_major_formatter(
        tck.FuncFormatter(lambda x, pos: '{:.0f}'.format(x*(min_speed-max_speed)))
    )
    
    configurations = getConfigurations(environment, model_name, data_name)

    lines = dict()
    points = dict()

    for name in configurations.keys():
        colors = ((0.5,0.5,0.5), (0.5,0.5,0.5))
        line, = ax.plot(x, y, color="r", label=name)
        pts, = ax.plot([], marker='o', color=colors[1], linestyle='None')

        lines[name] = line
        points[name] = pts

    while True:
        for name, config in configurations.items():
            if (config.data.update()):
                pts = config.data.points()
                if len(pts) > 0:
                    points[name].set_data(pts[:,0], pts[:,1])
                    print("Data storage for configuration '"+name+"' was updated")
                else:
                    points[name].set_data([[], []])
                    print("Data storage for configuration '"+name+"' was cleared")

            if (config.model.update()):
                model = config.model.model()
                if model != None:
                    lines[name].set_visible(True)
                    lines[name].set_ydata(model.apply(x))
                    print("Model storage for configuration '"+name+"' was updated")
                else:
                    lines[name].set_visible(False)
                    print("Model storage for configuration '"+name+"' was cleared")
        
        fig.canvas.draw()
        fig.canvas.flush_events()
        plt.pause(0.1)

def parse(rawargs) -> Dict[str, str]:
    args = {}
    for arg in rawargs:
        key, value = arg.split('=')
        args[key] = value
    
    return args

def getConfigurations(dir, model_name, data_name) -> Dict[str, ConfigurationStorage]:
    configs = dict()
    for name in os.listdir(dir):
        if isfile(name):
           continue
        cdir = os.path.join(dir, name) 

        model = ModelStorage(os.path.join(cdir, model_name), name)
        data = DataStorage(os.path.join(cdir, data_name), name)

        configs[name] = ConfigurationStorage(model, data)

    return configs

def createConfigurationColor(shade: float):
    r = random.random()
    g = random.random()
    b = random.random()

    return (
        (r, g, b), 
        (clamp(0, r * shade, 1), clamp(0, g * shade, 1), clamp(0, b * shade, 1))
    )

def clamp(min, x, max):
    return min if x < min else (max if x > max else x)


if __name__ == "__main__":
    main(parse(sys.argv[1:]))
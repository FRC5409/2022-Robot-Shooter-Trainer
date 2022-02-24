import sys

import matplotlib
import matplotlib.pyplot as plt
import matplotlib.ticker as tck

import numpy as np

from typing import Dict, Final
from storage import DataStorage, ModelStorage

DEFAULT_MODEL_STORAGE_FILE: Final = "model.csv"
DEFAULT_DATA_STORAGE_FILE: Final  = "data.csv"
DEFAULT_MAX_SPEED: Final          = "1000"
DEFAULT_MIN_SPEED: Final          = "0"
DEFAULT_MAX_DISTANCE: Final       = "10"
DEFAULT_MIN_DISTANCE: Final       = "0"
DEFAULT_BACKEND: Final            = "TkAgg"

def main(args):
    # Get arguments
    model_storage = ModelStorage(args.get("model_storage", DEFAULT_MODEL_STORAGE_FILE))
    data_storage = DataStorage(args.get("data_storage",  DEFAULT_DATA_STORAGE_FILE))

    max_speed = float(args.get("max_speed", DEFAULT_MAX_SPEED))
    min_speed = float(args.get("min_speed", DEFAULT_MIN_SPEED))

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
    
    prediction_line, = ax.plot(x, y, 'r-', color='gold') # Returns a tuple of line objects, thus the comma
    setpoints, = ax.plot([], marker='o', color='black', linestyle='None')

    ax.xaxis.set_major_formatter(
        tck.FuncFormatter(lambda x, pos: '{:.0f}'.format(x*(min_distance-max_distance)))
    )

    ax.yaxis.set_major_formatter(
        tck.FuncFormatter(lambda x, pos: '{:.0f}'.format(x*(min_speed-max_speed)))
    )

    plt.xlabel("Distance (ft)")
    plt.ylabel("Speed (rpm)")

    while True:
        if (data_storage.update()):

            points = data_storage.points()
            if len(points) > 0:
                setpoints.set_data(points[:,0], points[:,1])
                print("Data storage updated")
            else:
                setpoints.set_data([[], []])
                print("Data storage was cleared")

        if (model_storage.update()):
            model = model_storage.model()
            if model != None:
                prediction_line.set_visible(True)
                prediction_line.set_ydata(model.apply(x))
                print("Model storage updated")
            else:
                prediction_line.set_visible(False)
                print("Model storage was cleared")

        fig.canvas.draw()
        fig.canvas.flush_events()

        plt.pause(0.1)

def parse(rawargs) -> Dict[str, str]:
    args = {}
    for arg in rawargs:
        key, value = arg.split('=')
        args[key] = value
    
    return args

if __name__ == "__main__":
    main(parse(sys.argv[1:]))
import os

import numpy as np

from typing import Optional
from data import ModelParameters

class StorageBase:
    target: str

    def __init__(self, target: str) -> None:
        self.target = target

    def update(self) -> bool:
        raise NotImplementedError('subclasses must implement update()!')

class ModelStorage(StorageBase):
    _model: ModelParameters
    _configuration: str

    def __init__(self, target: str, configuration: str) -> None:
        super().__init__(target)
        self.configuration = configuration
        self._last_change_t = 0
        self._model = None

    def model(self) -> Optional[ModelParameters]:
        return self._model

    def update(self) -> bool:
        change_t = os.stat(self.target).st_mtime
        if change_t != self._last_change_t:
            self.__read();
            self._last_change_t = change_t;
            return True
        return False

    def __read(self):
        f = open(self.target, "r")

        line = f.readline()
        if line:       
            rawparams = line.split(',')
            if 'nan' in rawparams:
                self._model = None
            else:
                self._model = ModelParameters([float(x) for x in rawparams])
        else:
            self._model = None


class DataStorage(StorageBase):
    configuration: str
    _points: np.array
    _speed: np.array

    def __init__(self, target: str, configuration: str) -> None:
        super().__init__(target)
        self.configuration = configuration
        self._last_change_t = 0
        self._points = None

    def points(self) -> Optional[np.array]:
        return self._points

    def update(self) -> bool:
        change_t = os.stat(self.target).st_mtime
        if change_t != self._last_change_t:
            self.__read();
            self._last_change_t = change_t;
            return True
        return False

    def __read(self):
        f = open(self.target, "r")

        points = []

        for line in f.readlines():
            v = line.split(',')
            points.append([ float(v[0]), float(v[1]) ])

        self._points = np.array(points)

class ConfigurationStorage:
    def __init__(self, model: ModelStorage, data: DataStorage) -> None:
        self.model = model
        self.data = data
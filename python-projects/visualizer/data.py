from typing import Sized
from numpy import ndarray

class ModelParameters(Sized):
    _values: list[float]

    def __init__(self, values: list[float]) -> None:
        self._values = values;

    def __len__(self) -> int:
        return len(self._values)

    def __getitem__(self, index) -> float:
        return self._values[index]

    def size(self) -> int:
        return len(self._values)

    def apply(self, space: ndarray):
        return sum( (space**i) * self._values[i] for i in range(self.size()))
import os

DEFAULT_POINTS = [(0.2, 0.5), (0.4, 0.5), (0.6, 0.5)]

class DataStorage:
    target: str

    def __init__(self, target: str) -> None:
        self.target = target
        
    def read(self):
        if not os.path.exists(self.target):
            open(self.target, 'w').close()
            return list(DEFAULT_POINTS)

        points = []
    
        f = open(self.target, "r")

        for line in f.readlines():
            v = line.split(',')
            points.append([ float(v[0]), float(v[1]) ])
        
        f.close()

        if len(points) != 3:
            return list(DEFAULT_POINTS)

        return points
        
    def write(self, points):
        f = open(self.target, "w")

        f.writelines(f"{pt[0]}, {pt[1]}\n" for pt in points)

        f.close()

class OptionsStorage:
    target: str

    def __init__(self, target: str) -> None:
        self.target = target
        
    def read(self):
        options = dict()

        if not os.path.exists(self.target):
            open(self.target, 'w').close()
        else:
            f = open(self.target, "r")

            for line in f.readlines():
                key, value = line.split('=')
                options[key] = value

            f.close()

        return options
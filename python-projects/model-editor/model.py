
class DataModel:
    kA: float
    kB: float
    kA: float

    # http://chris35wills.github.io/parabola_python/
    def from_points(points):
        assert len(points) == 3

        A, B, C = points

        denom = (A[0]-B[0]) * (A[0]-C[0]) * (B[0]-C[0])
        if denom == 0:
            return DataModel(0,0,0)
        else:
            kA = (C[0] * (B[1]-A[1]) + B[0] * (A[1]-C[1]) + A[0] * (C[1]-B[1])) / denom
            kB = (C[0]*C[0] * (A[1]-B[1]) + B[0]*B[0] * (C[1]-A[1]) + A[0]*A[0] * (B[1]-C[1])) / denom
            kC = (B[0] * C[0] * (B[0]-C[0]) * A[1]+C[0] * A[0] * (C[0]-A[0]) * B[1]+A[0] * B[0] * (A[0]-B[0]) * C[1]) / denom
            
            return DataModel(kA, kB, kC)

    def __init__(self, kA=0, kB=0, kC=0) -> None:
        self.kA = kA
        self.kB = kB
        self.kC = kC

    def apply(self, x1, x2):
        return x2*self.kA + x1*self.kB + self.kC  
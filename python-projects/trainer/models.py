import torch
import torch.nn as nn


class Linear(nn.Module):
    def __init__(self):
        super().__init__()
        self.a = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.b = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))

    def forward(self, x):
        return self.a + self.b*x


class Quadratic(nn.Module):
    def __init__(self):
        super().__init__()
        self.a = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.b = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.c = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))

    def forward(self, x):
        return self.a + self.b*x + self.c*x**2


class Cubic(nn.Module):
    def __init__(self):
        super().__init__()
        self.a = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.b = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.c = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.d = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))

    def forward(self, x):
        return self.a + self.b*x + self.c*x**2 + self.d*x**3


class Quartic(nn.Module):
    def __init__(self):
        super().__init__()
        self.a = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.b = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.c = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.d = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.e = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))

    def forward(self, x):
        return self.a + self.b*x + self.c*x**2 + self.d*x**3 + self.e*x**4


class Quintic(nn.Module):
    def __init__(self):
        super().__init__()
        self.a = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.b = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.c = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.d = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.e = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))
        self.f = nn.Parameter(torch.randn(1, requires_grad=True, dtype=torch.float))

    def forward(self, x):
        return self.a + self.b*x + self.c*x**2 + self.d*x**3 + self.e*x**4 + self.f*x**5
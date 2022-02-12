import sys
import math

import numpy as np

import torch
import torch.optim as optim

from models import *


def get_data_random():
    # Data Generation
    np.random.seed(42)
    x = np.random.rand(100, 1)
    y = 1 + 2 * x + .1 * np.random.randn(100, 1)

    # Shuffles the indices
    idx = np.arange(100)
    np.random.shuffle(idx)

    # Uses first 80 random indices for train
    train_idx = idx[:80]
    # Uses the remaining indices for validation
    val_idx = idx[80:]

    # Generates train and validation sets
    x_train, y_train  = torch.from_numpy(x[train_idx]), torch.from_numpy(y[train_idx])
    x_val,   y_val    = torch.from_numpy(x[val_idx]), torch.from_numpy(y[val_idx])

    return x_train, y_train, x_val,   y_val


def get_data_file(path):
    file = open(path, 'r')

    x_in = []
    y_in = []

    for line in file.readlines():
        v = line.split(',')
        x_in.append(float(v[0]))
        y_in.append(float(v[1]))

    assert len(x_in) == len(y_in), AssertionError("Inputs are of different lengths.")

    x = torch.tensor(x_in, requires_grad=False, dtype=torch.double).reshape((len(y_in), 1))
    y = torch.tensor(y_in, requires_grad=False, dtype=torch.double).reshape((len(y_in), 1))

    idx = torch.randperm(len(x_in))

    # DO LATER MAYBE: split data into train and test sets
    x_train, y_train = x[idx[:]], y[idx[:]]
    x_test, y_test = x[idx[:]], y[idx[:]]

    return x_train, y_train, x_test, y_test


def fit(model, x_train, y_train):
    lr = 1e-1
    optimizer = optim.SGD(model.parameters(), lr=lr)
    loss_fn = nn.MSELoss(reduction='mean')

    n_epochs = 10000
    for epoch in range(n_epochs):

        y_pred = model(x_train)
        loss = loss_fn(y_train, y_pred)

        optimizer.zero_grad()
        loss.backward()
        optimizer.step()


def test(model, x_test, y_test):
    y_pred = model(x_test)
    error = abs(y_test - y_pred)/y_test
    return error.mean()


def save(model, path):
    file = open(path, 'w+')

    out = ''
    for _, value in model.state_dict().items():
        out += f'{value.item()},'
    out = out[:-1]

    file.write(out)


def main():
    in_path = './in.txt'
    out_path = './out.txt'

    degree = 1

    args = sys.argv[1:]
    print(args)

    # arg format: thing=value
    for arg in args:
        name, value = arg.split('=')
        print(name, value)
        if name == 'degree':
            degree = min(max(int(value), 0), 5)

        elif name == 'in_path':
            in_path = value

        elif name == 'out_path':
            out_path = value

    x_train, y_train, x_test, y_test = get_data_random() # get_data_file(in_path)

    model = None
    if degree == 1:
        model = Linear()
    elif degree == 2:
        model = Quadratic()
    elif degree == 3:
        model = Cubic()
    elif degree == 4:
        model = Quartic()
    elif degree == 5:
        model = Quintic()

    fit(model, x_train, y_train)
    # print(model.state_dict())
    print(f"Accuracy: {test(model, x_test, y_test)}")

    save(model, out_path)


if __name__ == "__main__":
    main()














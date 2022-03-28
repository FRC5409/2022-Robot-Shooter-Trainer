from typing import Dict

import colorsys
import random

def createRandomColor(shade: float):
    h = random.random()
    s = 1
    v = 1

    return colorsys.hsv_to_rgb(h,s,v)

def clamp(min, x, max):
    return min if x < min else (max if x > max else x)

def parse(rawargs) -> Dict[str, str]:
    args = {}
    for arg in rawargs:
        key, value = arg.split('=')
        args[key] = value
    
    return args

def get_digit(x, n):
    return x // 10**n % 10

def get_aspect(x, y, f=1):
    return (f*y/x, f) if x > y else (f, f*x/y)
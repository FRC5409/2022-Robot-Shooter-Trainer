from typing import Dict

import colorsys
import random

def createColorPair(value: float):
    hue = random.random()
    
    return (
        colorsys.hsv_to_rgb(hue, 1.0, 1.0),
        colorsys.hsv_to_rgb(hue, 1.0, value)
    )

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
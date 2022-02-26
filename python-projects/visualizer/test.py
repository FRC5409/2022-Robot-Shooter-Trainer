# Implementation of matplotlib function 
import numpy as np
from matplotlib.axis import Axis  
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
     
np.random.seed(19680801)
  
fig, ax = plt.subplots()
ax.plot(100*np.random.rand(20))
  
formatter = ticker.FormatStrFormatter('?%1.2f')
Axis.set_major_formatter(ax.yaxis, formatter)
  
for tick in ax.yaxis.get_major_ticks():
    tick.label1.set_color('green')
  
plt.title("Matplotlib.axis.Axis.set_major_formatter()\n\
Function Example", fontsize = 12, fontweight ='bold') 
  
plt.show()
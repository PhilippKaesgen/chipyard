import argparse

import matplotlib
import matplotlib.pyplot as plt
import numpy as np
from mpl_toolkits.mplot3d import axes3d

parser = argparse.ArgumentParser()
parser.add_argument('--weak', action='store_true')
parser.add_argument('--strong', action='store_true')

args = parser.parse_args()

if args.weak == False and args.strong == False:
    raise ValueError('specify either --weak or --strong')


if args.weak:
    from weak_float_20sep24_2 import data  # weak scaling data
    _, _, baseline = data[0]
    data = [ (a, b, baseline/c) for a, b, c in data[1:]]
else:
    from strong_float_20sep24 import data  # strong scaling data
    ta, tb, baseline = data[0]
    data[0] = (ta,tb,1)
    data[1:] = [ (a, b, baseline/c) for a, b, c in data[1:]]


def map_colors(p3dc, func, cmap='viridis'):
    """
    Color a tri-mesh according to a function evaluated in each barycentre.

    p3dc: a Poly3DCollection, as returned e.g. by ax.plot_trisurf
    func: a single-valued function of 3 arrays: x, y, z
    cmap: a colormap NAME, as a string

    Returns a ScalarMappable that can be used to instantiate a colorbar.
    """

    from matplotlib.cm import ScalarMappable, get_cmap
    from matplotlib.colors import Normalize
    from numpy import array

    # reconstruct the triangles from internal data
    x, y, z, _ = p3dc._vec
    slices = p3dc._segslices
    triangles = array([array((x[s], y[s], z[s])).T for s in slices])

    # compute the barycentres for each triangle
    xb, yb, zb = triangles.mean(axis=1).T

    # compute the function in the barycentres
    values = func(xb, yb, zb)

    # usual stuff
    norm = Normalize()
    colors = get_cmap(cmap)(norm(values))

    # set the face colors of the Poly3DCollection
    p3dc.set_fc(colors)

    # if the caller wants a colorbar, they need this
    return ScalarMappable(cmap=cmap, norm=norm)
#fig = plt.figure()
#ax = plt.axes(projection='3d')

# Grab some test data.
#X, Y, Z =  #axes3d.get_test_data(0.05)
func = lambda x,y,z: x*y

font = {'fontname': 'Roboto'}

x = [t for t,h,r in data] #[1,1,1,2,2,2,4,4,4,8,8,16] #np.logspace(0, 4, num=5, base=2)
y = [h for t,h,r in data] #[16,25,36, 16,25,36, 16,25,36, 16,25, 16] #np.array([16, 25, 36])

z = [r for t,h,r in data]

#colormap = [func(t,h,r) for t,h,r in data]
#mincm, maxcm = min(colormap), max(colormap)
#norm = matplotlib.colors.Normalize(mincm, maxcm)
#m = plt.cm.ScalarMappable(norm=norm, cmap='jet')
#m.set_array([])
#fcolors = m.to_rgba(colormap)

# Plot a basic wireframe.
#ax.plot_wireframe(X, Y, Z, rstride=10, cstride=10)

fig = plt.figure()
ax = fig.add_subplot(111, projection="3d")
p3dc = ax.plot_trisurf(x, y, z)
#mappable = map_colors(p3dc, func, 'jet')
#cbar =plt.colorbar(mappable, shrink=0.67, aspect=16.7)
#cbar.set_label('isometric total # PEs', **font)

ax.set_xlabel("# RoTs", **font)
ax.set_ylabel("# PEs per RoT", **font)
if args.weak:
    ax.set_zlabel("Efficiency", **font)
else:
    ax.set_zlabel("Speedup", **font)
plt.show()

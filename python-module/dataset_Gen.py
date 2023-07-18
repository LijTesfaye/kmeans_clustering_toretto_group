"""
main imports
"""
from sklearn.datasets import make_blobs
import pandas as pd
from pandas import DataFrame
from pandas.plotting._matplotlib import scatter_matrix
import numpy as np
from matplotlib import pyplot
""""
This file does the following  major things
1) It generated a made-up dataset
2) plots the clusters in different colors
3) saves the plot
"""
#
"""
These are the input values that we gonna vary to get a different dataset types.
"""
# basic variables of the kmeans
d_dimensions = 1
k_clusters = 1
n_samples =1

# the decimal places
decimal_places = 4
"""
Writing  the generated data to the file.
"""
X_data, y = make_blobs(n_samples=n_samples, n_features=d_dimensions, centers=k_clusters,cluster_std=1.2, random_state=42)
filename = f"data{d_dimensions}D{k_clusters}K{n_samples}N.txt"
with open(filename, "w") as file:
    for x_data in X_data:
        for value in range(d_dimensions):
            if value == (d_dimensions - 1):
                file.write(str(round(x_data[value], decimal_places)))
            else:
                file.write(str(round(x_data[value], decimal_places)) + ",")
        file.write("\n")
#
data = np.array(X_data)
"""
Scatter Plot definition
"""
df_scatter = pd.DataFrame(data, columns=[f'x_{i}' for i in range(d_dimensions)])
scatter_matrix(df_scatter, alpha=0.2, figsize=(10, 10))
"""
Plotting the clusters
"""
df = DataFrame(dict([(f'x_{i}', X_data[:, i]) for i in range(d_dimensions)] + [('label', y)]))
colors = {i: pyplot.cm.nipy_spectral(float(i) / k_clusters) for i in range(k_clusters)}
fig, ax = pyplot.subplots()
grouped = df.groupby('label')
for key, group in grouped:
    group.plot(ax=ax, kind='scatter', x=f'x_0', y=f'x_1', label=key, color=colors[key])
pyplot.legend()
pyplot.xlabel(f'({d_dimensions} dimensions)')
pyplot.ylabel(f'({k_clusters} clusters)')
pyplot.title(f'({n_samples} samples) Cluster Plot')
#
# Save the cluster plot as a PNG file
cluster_plot_filename = f"plot_clusters_{d_dimensions}D{k_clusters}K{n_samples}N.png"
pyplot.savefig(cluster_plot_filename)
# Show the cluster plot
pyplot.show()

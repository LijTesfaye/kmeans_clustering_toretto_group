import numpy as np
import pandas as pd
from matplotlib import pyplot
from sklearn.cluster import KMeans
import os

d_dimensions=2
k_clusters = 4
n_samples=1000

# Generate the input file path
#for other datasets
inputfile = f"data{d_dimensions}D{k_clusters}K{n_samples}N.txt"

# Load the data from the file
data = np.loadtxt(inputfile, delimiter=',')

# Perform KMeans clustering
kmeans = KMeans(n_clusters=k_clusters, random_state=42)
labels = kmeans.fit_predict(data)

df = pd.DataFrame(dict([(f'x_{i}', data[:, i]) for i in range(d_dimensions)] + [('label', labels)]))
colors = {i: pyplot.cm.nipy_spectral(float(i) / k_clusters) for i in range(k_clusters)}
fig, ax = pyplot.subplots()
grouped = df.groupby('label')
for key, group in grouped:
    group.plot(ax=ax, kind='scatter', x=f'x_0', y=f'x_1', label=key, color=colors[key])
pyplot.legend()
pyplot.xlabel(f'({d_dimensions} dimensions)')
pyplot.ylabel(f' ({k_clusters} clusters)')
pyplot.title(f'[Made up dataset] ({n_samples} samples) Scatter Graph')

# Save the plot with a specific filename
output_filename = f'plot_{d_dimensions}D{k_clusters}K{n_samples}N.png'
if os.path.exists(output_filename):
    os.remove(output_filename)  # Remove the existing file
pyplot.savefig(output_filename)
# Display the plot
pyplot.show()

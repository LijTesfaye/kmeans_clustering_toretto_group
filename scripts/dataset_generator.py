"""
main imports
"""
from sklearn.datasets import make_blobs
import pandas as pd
import numpy as np
from pandas.plotting._matplotlib import scatter_matrix
from matplotlib import pyplot
from pandas import DataFrame
"""
These are the input values that we gonna vary to get a different set of values.
"""
d_dimensions = 2
n_samples = 1000
k_means = 4
# the decimal places
decimal_places = 4
"""
Write the generated dataset to a file.
"""
X_data, y = make_blobs(n_samples=n_samples, centers=k_means, n_features=d_dimensions, random_state=42)
with open("data2D4means.txt", "w") as file:
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
Scatter Plot
"""
df_scatter = pd.DataFrame(data, columns=['x_0', 'x_1'])
scatter_matrix(df_scatter, alpha=0.2, figsize=(10, 10))
"""
The data in the clustered form is plotted here after.
"""
df = DataFrame(dict(x=X_data[:, 0], y=X_data[:, 1], label=y))
colors = {0: 'black', 1: 'blue', 2: 'green', 3: 'orange', 4: 'purple', 5: 'red', 6: 'pink'}
fig, ax = pyplot.subplots()
grouped = df.groupby('label')
for key, group in grouped:
    group.plot(ax=ax, kind='scatter', x='x', y='y', label=key, color=colors[key])
pyplot.show()


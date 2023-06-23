from sklearn.cluster import KMeans
import numpy as np
import time
from statistics import mean
import pandas as pd
from matplotlib import pyplot
points = []
clusters = 4

start_milli_time = round(time.time() * 1000, 4)
with open("data2D4means.txt", "r") as file:
    for line in file:
        comps = line.split(",")
        point = [float(comps[i]) for i in range(len(comps))]
        points.append(point)

dataset = np.array(points)
kmeans = KMeans(n_clusters=clusters, init='random', random_state=42).fit(dataset)

end_milli_time = round(time.time() * 1000, 4)
execution_time = round(end_milli_time - start_milli_time, 4)

print(f"Execution time:{str(execution_time)} ms")
print(f"cluster_centers:{str(kmeans.cluster_centers_)}")
print(str(f"{kmeans.n_iter_}"))

# plot as black points and red centroids
cent = kmeans.cluster_centers_
centr = np.array(cent)

# concatenate the points with the centroids
c = np.vstack([dataset, centr])

df = pd.DataFrame({'x': c[:, 0], 'y': c[:, 1]})

# show the all the elements in the 'wc' as black and the 'means' as red
labels = []
for i in range(0, 1000):
    labels.append('black')
for i in range(0, 4):
    labels.append('red')

df['label'] = labels

print(df)

df.plot(x='x', y='y', c=df['label'], kind='scatter')
pyplot.show()

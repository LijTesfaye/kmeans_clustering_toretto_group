import numpy as np
# Example usage
d_dimensions=2
k_clusters = 4
n_samples=1000
#
data_file = f"data{d_dimensions}D{k_clusters}K{n_samples}N.txt"
output_file = f"ic{d_dimensions}D{k_clusters}K{n_samples}N.txt"

data = np.loadtxt(data_file, delimiter=',')  # Load data from the text file

num_dimensions = data.shape[1]
print("Number of dimensions:", num_dimensions)

# Generate random initial centroids
np.random.seed()  # Use system time as the random seed
centroids = np.random.permutation(data)[:k_clusters]

# Write initial centroids to the output file with four decimal places
np.savetxt(output_file, centroids, delimiter=',', fmt='%.4f')
print(f"Initial centroids saved to {output_file}")

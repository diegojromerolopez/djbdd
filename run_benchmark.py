#!/usr/bin/python

import os
import subprocess


def run_batch():
	algorithms = [
		"sifting",
		"sifting_sameorder",
		"sifting_randomorder random_seed=10"
		"sifting_randomorder random_seed=327489"
		"sifting_randomorder random_seed=121481"
		
		"window_permutation window_size=2", "window_permutation window_size=3", "window_permutation window_size=4",
		
		# Genetica algorithm
		"genetic random_seed=10 generations=100, population=5 selection_percentage=0.5 mutation_probability=0.2",
		"genetic random_seed=327489 generations=100, population=5 selection_percentage=0.5 mutation_probability=0.2",
		"genetic random_seed=121481 generations=100, population=5 selection_percentage=0.5 mutation_probability=0.2",
		
		"memetic random_seed=10 population=8 generations=100 selection_percentage=0.2 mutation_probability=0.1",
		"memetic random_seed=327489 population=8 generations=100 selection_percentage=0.2 mutation_probability=0.1",
		"memetic random_seed=121481 population=8 generations=100 selection_percentage=0.2 mutation_probability=0.1",
		
		"random_swapper random_seed=10 iterations=100",
		"random_swapper random_seed=327489 iterations=100",
		"random_swapper random_seed=121481 iterations=100",
	]
	for algorithm in algorithms:
		command = r"java -jar './DJBDD/store/DJBDD.jar' --memory-optimization-benchmark --dimacs ./data/benchmarks {1}".format(file, algorithm)
		print command
		output = subprocess.check_output(command, stderr=subprocess.STDOUT, shell=True)
		print output

if __name__ == "__main__":
	run_batch()


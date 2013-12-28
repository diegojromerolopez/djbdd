#!/usr/bin/python

import os
import subprocess


def run_batch():
	algorithms = [
		#"total_search",
		"sifting", "sifting_sameorder",
		"window_permutation window_size=2", "window_permutation window_size=3", "window_permutation window_size=4",
		"genetic generations=100, population=1000 selection_percentage=0.5 mutation_probability=0.2",
		"genetic generations=1000, population=100 selection_percentage=0.2 mutation_probability=0.1",
		"random_swapper iterations=1000"
	]
	for root, dirs, files in os.walk("data/benchmarks"):
		for dir in dirs:
			print dir
			for algorithm in algorithms:
				command = r"java -jar './DJBDD/store/DJBDD.jar' --memory-optimization-benchmark --dimacs ./data/benchmarks/{0} {1}".format(dir, algorithm)
				#command = r"java -jar './DJBDD/dist/DJBDD.jar'"
				#command = "java"
				print command
				output = subprocess.check_output(command, stderr=subprocess.STDOUT, shell=True)
				print output
				return

if __name__ == "__main__":
	run_batch()


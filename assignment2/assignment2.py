import fileinput
import glob


for line in fileinput.input(glob.glob("part-r-0000*")):
    lineseg = line.split()
    if line.find("cloud") != -1:
        print line
        
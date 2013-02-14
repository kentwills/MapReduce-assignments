import fileinput
import glob
  
cloudlist = []
value=0
for line in fileinput.input(glob.glob("part-r-0000*")):
	lineseg=line.split();
	'''	line = line.replace(")","").replace("(","").replace(",","")
	lineseg = line.split()
	for l in lineseg:
		l=l.strip()
	'''
	'''if lineseg[0].split(",")[0]==("love"):       					'''
	if lineseg[0].find("*")==-1:
		value=float(lineseg[1])
		cloudlist.append((lineseg[0],value))
		'''print((lineseg[0]+","+lineseg[1],value))'''
		 
cloudlist.sort(key=lambda tup: tup[1])
count =len(cloudlist)
print(count)
for x in range(count-100,count-1):
	l= cloudlist[x]
	print(l[0]+","+str(l[1]))


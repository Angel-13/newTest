JC = javac
JVM = java
FLAGS = -verbose
JVP = javap
MAIN = Test1
CLASS = ./Milestone6.class
sources = $(wildcard Test1.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /scanner/*java /symbolTable/*.java /tokens/*.java)
classes = $(sources:.java=.class)
all: $(classes)
clean:
	rm -f *.class
%.class: %.java
	$(JC) $(sources)
	$(JVM) $(MAIN)
	$(JVP) $(FLAGS) $Milestone6.class
JC = javac
JVM = java
MAIN = Test1
sources = $(wildcard *.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /scanner/*java /symbolTable/*.java /tokens/*.java)
classes = $(sources:.java=.class)
all: $(classes)
clean:
	rm -f *.class
%.class: %.java
	$(JC) $(sources)
	$(JVM) $(MAIN)
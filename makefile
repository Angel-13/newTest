JC = javac
JVM = java
MAIN = Test1
sources = $(wildcard *.java /scanner/*java /tokens/*.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /symbolTable/*.java)
classes = $(sources:.java=.class)
all: $(classes)
clean:
	rm -f *.class
%.class: %.java
	$(JC) $(MAIN)
	$(JVM) $(MAIN)
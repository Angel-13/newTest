JC = javac
JVM = java
MAIN = Test
sources = $(wildcard /*.java /scanner/*java /tokens/*.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /symbolTable/*.java)
classes = $(sources:.java=.class)
all: $(classes)
%.class: %.java
	$(JC) $<
	$(JVM) $(MAIN)
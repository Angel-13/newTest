FOO = $PATH
JC = javac
JVM = java
MAIN = Test1
sources = $(wildcard *.java /scanner/*java /tokens/*.java )
classes = $(sources:.java=.class)
all: $(classes)
%.class: %.java
	$(JC) $<
	$(JVM) $(MAIN)
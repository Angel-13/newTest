FOO = $PATH
JC = $javac
JVM = $java
sources = $(wildcard *.java /scanner/*java /tokens/*.java )
classes = $(sources:.java=.class)
all: $(classes)
clean:
	rm -f *.class
%.class: %.java
	$(JC) $<
	$(JVM) $(MAIN)
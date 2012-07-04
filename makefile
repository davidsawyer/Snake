compile:
	@javac -d build src/*.java
run:
	@java -cp build Snake
jar:
	@cd build; jar cmf MainClass.txt Snake-1.x.jar *.class; mv Snake-1.x.jar ../releases; cd ..
clean:
	@rm build/*.class; rm .DS_Store; rm src/.DS_Store; rm build/.DS_Store; rm releases/.DS_Store; rm images/.DS_Store

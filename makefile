CC=javac

class-path=lib/PacSimLib.jar
build-path=bin
main-class=Assignment1

tiny=lib/tsp-tiny
sparse=lib/tsp-alley-sparse


compile:
	make build-only
	java -cp "$(build-path):$(class-path)" $(main-class) $(sparse)


tiny:
	#make build-testing-package
	make build-only
	java -cp "$(build-path):$(class-path)" $(main-class) $(tiny)


build-only:
	rm -rf $(build-path)
	mkdir $(build-path)
	find src -name "*.java" | xargs $(CC) -Xlint -cp $(class-path) -d $(build-path) 

build-testing-package:
	rm -rf $(build-path)
	mkdir $(build-path)
	find src/testing -name "*.java" | xargs $(CC) -Xlint -cp $(class-path) -d $(build-path) 

run:
	java -cp "$(build-path):$(class-path)" $(main-class) $(sparse)

run-tiny:
	java -cp "$(build-path):$(class-path)" $(main-class) $(tiny)

demo:
	java -cp "$(build-path):$(class-path)" PacSimReplan $(sparse)

demo-tiny:
	java -cp "$(build-path):$(class-path)" PacSimReplan $(tiny)

clean:
	rm -rf $(class-path)

SHELL := /bin/bash

.PHONY: run test coverage verify build clean

run:
	./mvnw spring-boot:run

test:
	./mvnw test

coverage:
	./mvnw test jacoco:report && echo "Open target/site/jacoco/index.html"

verify:
	./mvnw verify

build:
	./mvnw clean package

clean:
	./mvnw clean


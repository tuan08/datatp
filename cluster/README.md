#Install Dependencies#

```
sudo apt install python-pip
pip install click
pip install tabulate

```
#How To Run

Run the docker environment

````

$docker-machine env

$docker-machine start

````

Run the cluster command to test kafka

````
#To setup docker environement such network routing...
./cluster.py docker:setup


#Build the images
./cluster.py --config=config/test-kafka-config.json image --clean --build

#Run the test cluster
./cluster.py --config=config/test-kafka-config.json destroy server --start service --install --start status
````


To run the python unit test

````
#test single file
nosetests tests/util/TestFileUtil.py


#Test all unit test in a directory
nosetests tests/util
````

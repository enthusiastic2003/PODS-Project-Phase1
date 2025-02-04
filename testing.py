import unittest
import requests as re
import json
import pandas as pd

def get_data():
    url = "http://localhost:8080"
    endpoint_products = "/products"
    response = re.get(url + endpoint_products)
    return pd.DataFrame(json.loads(response.content))

class TestJson(unittest.TestCase):
    def test_json(self):
        expected_json = pd.read_csv("products.csv")
        self.assertTrue(expected_json.equals(get_data()))

class CustomTestResult(unittest.TextTestResult):
    def addSuccess(self, test):
        super().addSuccess(test)
        if test.id().endswith("TestJson.test_json"):
            self.stream.writeln("EndPoint Tested: /products")
            self.stream.writeln("Result: Success")

    def addFailure(self, test, err):
        super().addFailure(test, err)
        if test.id().endswith("TestJson.test_json"):
            self.stream.writeln("EndPoint Tested: /products")
            self.stream.writeln("Result: Failure")

    def addError(self, test, err):
        super().addError(test, err)
        if test.id().endswith("TestJson.test_json"):
            self.stream.writeln("EndPoint Tested: /products")
            self.stream.writeln("Result: Error")

class CustomTestRunner(unittest.TextTestRunner):
    resultclass = CustomTestResult

if __name__ == '__main__':
    unittest.main(testRunner=CustomTestRunner(), verbosity=2)

classA=dbo:$1

query="CONSTRUCT+{${classA}+?p+?range}+WHERE+{?p+rdfs:range+?range.+FILTER+regex(?range,+\"^http://www.w3.org/2001/XMLSchema%23\",+\"i\")+{SELECT+DISTINCT+?p+WHERE+{+[]+a+${classA};+?p+[].+}+}+}"

echo \#${query}

curl -g "http://dbpedia.org/sparql?query=${query}&timeout=120000"

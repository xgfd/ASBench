classA=dbo:$1

query="CONSTRUCT+{${classA}+?p+?range}+WHERE+{+[]+a+${classA};+?p+[].+?p+rdfs:range+?range.+FILTER+regex(?range,+\"^http://www.w3.org/2001/XMLSchema#\",+\"i\")}"

echo \#${query}

curl -g "http://dbpedia.org/sparql?query=${query}&timeout=120000"

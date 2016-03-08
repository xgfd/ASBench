classA=dbo:$1
classB=dbo:$2

query="CONSTRUCT+{${classA}+?p+${classB}}+WHERE+{+[]+a+${classA};+?p+[+a+${classB}].+}"

echo \#${query}

curl -g "http://dbpedia.org/sparql?query=${query}&timeout=120000"

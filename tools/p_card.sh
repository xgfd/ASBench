self=$0
sh execQ.sh -H 'Accept: text/csv' -G 'https://linkeddata1.calcul.u-psud.fr/sparql?timeout=600000' ./queries/${self%%.*}.rq \<${1?A predicate is required}\>

# compute the cardinality of a predicate
# results are returned as a csv with the following headers:
# p(predicate) s_count(count of distinct subs) o_count(count of distinct objs) t(count of triples) card(predicate cardinality) inverseCard(inverse cardinality) 
self=$0
sh execQ.sh -H 'Accept: text/csv' -G 'https://linkeddata1.calcul.u-psud.fr/sparql?timeout=600000' ./queries/${self%%.*}.rq \<${1?A predicate is required}\>
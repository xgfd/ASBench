# compute the cardinality of a predicate
# results are returned as a csv with the following headers:
# p(predicate) s_count(count of distinct subs) o_count(count of distinct objs) t(count of triples) card(predicate cardinality) inverseCard(inverse cardinality) 
self=$0
sh execQ.sh -H 'Accept: text/csv' -G "${1?SPARQL endpoint is required}" ./queries/${self%%.*}.rq ${2?A predicate is required}
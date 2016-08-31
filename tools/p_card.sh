self=$0
sh execQ.sh -H 'Accept: text/csv' ./queries/${self%%.*}.rq \<${1?A predicate is required}\>
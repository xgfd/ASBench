self=$0
sh execQ.sh ./queries/${self%%.*}.rq \<${1?A predicate is required}\>

p="$1"
fn="${p##*/}"

echo "Retriving RDF..."
sh p.sh $p > ../ttl/$fn.ttl

cd ..

echo "Converting RDF to dot..."
sh viz.sh ttl/$fn.ttl --t > ttl/$fn.dot

cd ttl
base=$(pwd)

cd ../tools

# echo $base/$fn.dot

echo "Plotting..."
./plot.m "$base/$fn.dot" "$base/$fn.pdf"
echo "All files are written to $base"
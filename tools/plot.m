#!/Applications/Mathematica.app/Contents/MacOS/wolframscript

in  = $ScriptCommandLine[[2]];
out = $ScriptCommandLine[[3]];
# Print[in]
# Print[out]

Export[out, Import[in, VertexLabels -> None, ImageSize -> 2000]]

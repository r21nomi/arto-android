precision highp float;

varying vec2 texcoordVarying;

#extension GL_OES_standard_derivatives : enable

#define TWO_PI  6.283

uniform vec2 resolution;
uniform float time;

float random(vec2 st) {
	return fract(sin(dot(st, vec2(12.9898, 78.233))) * 43758.5453);
}

vec2 st2Triangle(vec2 st) {
	float sx = st.x - st.y / 2.0; // skewed x
	float sxf = fract(sx);
	float offs = step(fract(1.0 - st.y), sxf);
	return vec2(floor(sx) * 2.0 + offs, st.y);
}

float triangle(vec2 st) {
	float sp = random(floor(st2Triangle(st)));
	return max(0.0, sin(sp * time));
}

void main() {
	vec2 st = (gl_FragCoord.xy * 2.0 - resolution) / min(resolution.x, resolution.y);

	st *= 10.0;  // Make pattern.
	st.y -= time * 2.0;  // Move

	float triangles = triangle(st);  // Make triangles.

	vec3 color = vec3(0.5, triangles, 0.9);

	gl_FragColor = vec4(color, 1.0);
}
precision highp float;

varying vec2 texcoordVarying;

#extension GL_OES_standard_derivatives : enable

#define TWO_PI  6.283

uniform vec2 resolution;
uniform float time;

float random (in float x) {
    return fract(sin(x) * 1e4);
}

float random (in vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898,78.233)))* 43758.5453123);
}

float patternize(vec2 st, vec2 v, float t) {
    vec2 p = floor(st+v);
    return step(t, random(100.+p*.000001)+random(p.x)*0.5);
}

void main( void ) {
	vec2 st = gl_FragCoord.xy / resolution.xy;
	st.x *= resolution.x / resolution.y;
	vec3 color;

	vec2 ipos = floor(st);
	vec2 fpos = fract(st);

	vec2 grid = vec2(100.0, 20.0);
	st *= grid;
	st /= vec2(1.0, 0.01);

	vec2 vel = vec2(time * max(grid.x, grid.y));
	vel *= vec2(-1.0, 0.0) * random(1.0+ipos.y);

	color = vec3(patternize(st, vel, 0.5), 1.0, 1.0);

	gl_FragColor = vec4(color, 1.0);
}
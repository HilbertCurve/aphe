#type vertex
#version 430 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec4 aColor;

flat out vec4 fColor;

uniform mat4 uView;
uniform mat4 uProjection;

void main()
{
    fColor = aColor;

    gl_Position = uProjection * uView * vec4(aPos, 1.0, 1.0);
}

#type fragment
#version 430 core
flat in vec4 fColor;

out vec4 color;

void main()
{
    color = vec4(fColor);
}

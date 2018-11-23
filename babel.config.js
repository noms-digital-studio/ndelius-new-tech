const presets = [
    [
        "@babel/env",
        {
            targets: {
                edge: "17",
                ie: "11"
            },
            useBuiltIns: "usage"
        }
    ],
    "@babel/preset-react"
];

const plugins = [
    "@babel/plugin-transform-modules-commonjs"
];

module.exports = { presets, plugins };
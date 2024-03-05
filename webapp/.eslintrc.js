module.exports = {
  env: {
    browser: true,
    es2021: true,
  },
  extends: ['airbnb', 'prettier'],
  overrides: [
    {
      env: {
        node: true,
      },
      files: ['.eslintrc.{js,cjs}'],
      parserOptions: {
        sourceType: 'script',
      },
    },
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
  },
  rules: {
    'react/jsx-filename-extension': 0,
    'react/jsx-props-no-spreading': 0,
    'arrow-body-style': 0,
    'react/prop-types': 0,
    'import/no-unresolved': 0,
    'react/no-unknown-property': 0,
    'react/destructuring-assignment': 0,
    'react/no-this-in-sfc': 0,
    'no-nested-ternary': 0,
    'no-lonely-if': 0,
    'react/jsx-no-bind': 0,
  },
  settings: {
    'import/resolver': {
      node: {
        extensions: ['.js', '.jsx'],
        moduleDirectory: ['node_modules', 'src/'],
      },
    },
  },
};

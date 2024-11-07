const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    mode: 'development',
    entry: './src/pintura.js', // Điểm nhập cho ứng dụng của bạn
    output: {
        path: path.resolve(__dirname, 'src/main/resources/static/node_modules/@pqina/pintura'),
        filename: 'pintura.js' // Tệp JS kết quả
    },
    plugins: [
        new CopyWebpackPlugin({
            patterns: [
                { from: 'node_modules/@pqina/pintura', to: '.' } // Sao chép các tệp vào đúng thư mục
            ]
        })
    ],
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            }
        ]
    }
};

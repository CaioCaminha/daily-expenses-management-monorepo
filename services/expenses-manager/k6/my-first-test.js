import http from 'k6/http';
import { sleep } from 'k6';
import { check } from 'k6';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';

export const options = {
    //virtual users - k6 runs multiple interactions in parallel with virtual users - more parallel traffic
    vus: 100,
    // duration: '30s',
    iterations: 100000,
}

const csvFile = open('statement.csv')

export default function () { 
    
    const url = 'http://localhost:8089/v1/statement'
    const fd = new FormData();
    
    fd.append('csv', http.file(csvFile, 'statement.csv', 'text/csv'))

    const res = http.post(url,
        fd.body(),
        {
            headers: { 'Content-Type': 'multipart/form-data; boundary=' + fd.boundary },
        }
    )

    // console.log(res.status)
    
    check(res, {
        'is status 201': (r) => r.status == 201,
    });

}

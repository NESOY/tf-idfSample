import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by nesoy on 2017. 11. 7..
 */
public class Tf_idfExample {
    private static String[] dataList = {
            "I love dogs",
            "I hate dogs and knitting",
            "Knitting is my hobby and my passion",
    };

    public static void main(String[] args) {
        ArrayList<HashMap<String, Tf_idf>> documentList = new ArrayList<>();

        // Tf 계산
        for (String data : dataList) {
            // Normalize - 자연어 처리
            CharSequence normalized = OpenKoreanTextProcessorJava.normalize(data);

            // Tokenize - 자연어 처리
            Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

            // documentMap 생성
            HashMap<String, Tf_idf> documentMap = new HashMap<String, Tf_idf>();

            // documentMap에 Tf값 입력
            for (String token : OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens)) {
                if (!documentMap.containsKey(token)) {  // token이 없을 경우
                    Tf_idf tokenValue = new Tf_idf();
                    tokenValue.addTf();
                    documentMap.put(token, tokenValue);
                } else {                                // token이 있는 경우
                    Tf_idf tokenValue = documentMap.get(token);
                    tokenValue.addTf();
                }
            }

            // documentList에 documentMap 추가
            documentList.add(documentMap);
        }

        // idf 계산
        for (HashMap documentMap : documentList) {
            Iterator<String> tokenList = documentMap.keySet().iterator();

            while (tokenList.hasNext()) {
                String token = tokenList.next();
                Tf_idf tokenValue = (Tf_idf) documentMap.get(token);

                int hit_document = 0;

                for (int index = 0; index < documentList.size(); index++) {
                    if (documentList.get(index).containsKey(token)) { // document에 token이 포함한 경우
                        hit_document++;
                    }
                }
                tokenValue.setIdf(Math.log10((double) documentList.size() / hit_document)); //  log(전체 문서 / hit 문서)
            }
        }


        // result
        int index = 0;
        for (HashMap documentMap : documentList) {
            Iterator<String> tokenList = documentMap.keySet().iterator();

            index++;
            System.out.println("======doucment" + index + "=======");
            while (tokenList.hasNext()) {
                String token = tokenList.next();
                Tf_idf tokenValue = (Tf_idf) documentMap.get(token);
                System.out.println("token = " + token + "  " + tokenValue);
            }
        }

    }
}


class Tf_idf {
    private int tf = 0;
    private double idf = 0;

    public void addTf() {
        this.tf = tf + 1;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public double getTf_idf() {
        return tf * idf;
    }

    @Override
    public String toString() {
        return String.format("tf = %d | idf = %.2f | tf-idf = %.2f", tf, idf, getTf_idf());
    }
}

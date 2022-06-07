package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.Random;

public class flappybird extends ApplicationAdapter {
	//variaveis
	private SpriteBatch batch;
	private Texture []  passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture moedaPrata;
	private Texture moedaOuro;
	private Texture moedaAtual;
	private Texture logo;


	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;
	private Circle circuloMoeda;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posicaoInicialVerticalPassaro=0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;
	private Random random;
	private int pontos=0;
	private int pontuacaoMaxima=0;
	private boolean passouCano=false;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;
	private float escalaMoeda= 0.25f;
	private float posicaoMoedaY;
	private float posicaoMoedaX;

	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound coinSound;

	Preferences preferencias;

	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;

	@Override
	//pra quando iniciar a aplicação executar algo uma vez
	public void create () {
		inicializarTexturas();
		inicializaObjetos();
	}

	@Override
	//enquanto a aplicação estiver rodando o metodo estara funcionando
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT| GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	//o metodo puxa a textura da pasta assets e aplica na variavel
	private void inicializarTexturas()
	{
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		moedaPrata = new Texture("moedeprata.png");
		moedaOuro = new Texture("moedadeouro.png");
		moedaAtual = moedaPrata;
		logo = new Texture("logo.png");
	}


	//estancia os objetos
	private void inicializaObjetos()
	{
		batch = new SpriteBatch();
		random = new Random();

		//busca a dimensão do dispositivo
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo/2;
		posicaoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 350;

		//aplica fontes nas variaveis, muda o tamanho e aplica cor
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);


		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);

		//estancia os colisores
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();
		circuloMoeda = new Circle();

		//o metodo puxa todos os sons da pasta assets e aplica na variavel
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		coinSound= Gdx.audio.newSound(Gdx.files.internal("coinsound.mp3"));


		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima",0);

		//estancia o tipo da camera
		camera = new OrthographicCamera();

		//seta posição da camera
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2,0);

		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		posicaoMoedaY = alturaDispositivo/2;
		posicaoMoedaX = larguraDispositivo/2;
	}


	//Verificando o estado do jogo e retorna o que acontece
	private void verificarEstadoJogo(){

		boolean toqueTela = Gdx.input.justTouched();
		//estado 0 ele verifica o input do jogador para poder iniciar a partida
		if (estadoJogo == 0) {
			if (toqueTela) {
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
		}

		//estado do jogo ao iniciar a partida
		else if (estadoJogo==1)
		{
			//movimenta o personagem
			if (toqueTela){
				gravidade = -15;
				somVoando.play();
			}

			//movimentação do cano na tela
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime()*200;
			//quando o objeto chega ao fim da tela ele retorna ao começo e aleatoriza os ponto
			posicaoMoedaX-=Gdx.graphics.getDeltaTime()*200;

			if (posicaoCanoHorizontal < -canoTopo.getWidth())
			{
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVertical  =random.nextInt(400) - 200;
				passouCano = false;
			}

			if (posicaoMoedaX < -moedaAtual.getWidth() * escalaMoeda / 2)
			{
				resetaMoeda();
			}

			//quando tocar na tela o personagem sobe
			if (posicaoInicialVerticalPassaro> 0 || toqueTela)
			{
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			}
			//puxa o personagem para baixo
			gravidade++;
		}
		else if (estadoJogo == 2)
		{
			if (pontos > pontuacaoMaxima)
			{
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
				preferencias.flush();
			}
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*500;

			if (toqueTela)
			{
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo/2;
				posicaoCanoHorizontal = larguraDispositivo;
				resetaMoeda();
			}
		}
	}

	private void resetaMoeda()
	{
		posicaoMoedaX = posicaoCanoHorizontal + larguraDispositivo/2;
		posicaoMoedaY= alturaDispositivo/2;

		if(random.nextInt(99) <= 29) moedaAtual = moedaOuro;
		else moedaAtual = moedaPrata;
	}

	private void  detectarColisoes()
	{
		circuloPassaro.set(
				50 + posicaoHorizontalPassaro + passaros [0].getWidth()/2/2,
				posicaoInicialVerticalPassaro + passaros[0].getHeight()/2/2,
				passaros[0].getWidth()/2/2
		);
		retanguloCanoBaixo.set(
				posicaoCanoHorizontal,
				alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		retanguloCanoCima.set(
				posicaoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/ 2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		circuloMoeda.set(posicaoMoedaX, posicaoMoedaY, moedaAtual.getWidth()*escalaMoeda/2);

		boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
		boolean colidiuMoeda = Intersector.overlaps(circuloPassaro, circuloMoeda);

		if (colidiuCanoCima|| colidiuCanoBaixo){
			if (estadoJogo == 1){
				somColisao.play();
				estadoJogo=2;
			}
		}
		if (colidiuMoeda == true)
		{
			if(moedaAtual == moedaOuro) pontos += 10;
			else pontos += 5;

			posicaoMoedaY = alturaDispositivo * 2;
			coinSound.play();
		}
	}
	private void desenharTexturas()
	{
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(fundo,0,0,larguraDispositivo,alturaDispositivo);
		batch.draw(passaros[ (int) variacao], 50 + posicaoHorizontalPassaro,posicaoInicialVerticalPassaro,passaros[ (int) variacao].getWidth()/2, passaros[ (int) variacao].getHeight()/2);
		batch.draw(canoBaixo, posicaoCanoHorizontal,
				alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanoHorizontal,
				alturaDispositivo/2+espacoEntreCanos/2+posicaoCanoVertical);
		textoPontuacao.draw(batch,String.valueOf(pontos), larguraDispositivo/2, alturaDispositivo-110);

		//desenhar moedas
		batch.draw(moedaAtual,posicaoMoedaX-moedaAtual.getWidth()*escalaMoeda/2, posicaoMoedaY-moedaAtual.getHeight()*escalaMoeda/2, moedaAtual.getWidth()*escalaMoeda,moedaAtual.getHeight()*escalaMoeda);

		//desenha a textura
		if (estadoJogo == 0)
		{
			batch.draw(logo, larguraDispositivo / 2 - logo.getWidth() / 2, alturaDispositivo / 2);
		}



		if (estadoJogo ==2 )
		{
			batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
			textoReiniciar.draw(batch, "Toque para reiniciar", larguraDispositivo/2 - 140, alturaDispositivo/2 - gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch, "Seu record é: " + pontuacaoMaxima + " pontos", larguraDispositivo/2 -140, alturaDispositivo/2 - gameOver.getHeight());
		}

		batch.end();
	}

	public void validarPontos()
	{
		if (posicaoCanoHorizontal < posicaoHorizontalPassaro){
			if (!passouCano) {

				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}
		variacao+= Gdx.graphics.getDeltaTime() * 10;

		if (variacao>3)
			variacao = 0;
	}
	@Override
	public void resize(int width, int height){
		viewport.update(width, height);
	}
	@Override
	public void dispose () {

	}
}

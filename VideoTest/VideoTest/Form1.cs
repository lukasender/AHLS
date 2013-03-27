using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

using AForge.Video;
using AForge.Video.DirectShow;
using System.Net;
using System.IO;
using System.Collections;


namespace VideoTest
{
    public partial class Form1 : Form
    {
        //Anlegen globaler Variablen
        VideoCaptureDevice videoSource;
        string highestSolution;
        Bitmap oldPic;
        Bitmap emptyPic;
        bool show = false;
        HttpPost httpPost;

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            //initialisieren der globalen Variablen
            emptyPic = (Bitmap)Image.FromFile("White.jpg");
            highestSolution = "0;0";

            //Anlegen einer Liste mit allen Videoquellen. (Hier können neben der gewünschten Webcam
            //auch TV-Karten, etc. auftauchen)
            FilterInfoCollection videosources = new FilterInfoCollection(FilterCategory.VideoInputDevice);

            //Überprüfen, ob mindestens eine Aufnahmequelle vorhanden ist
            if (videosources != null)
            {
                //Die erste Aufnahmequelle an unser Webcam Objekt binden
                //(habt ihr mehrere Quellen, muss nicht immer die erste Quelle die
                //gewünschte Webcam sein!)
                videoSource = new VideoCaptureDevice(videosources[0].MonikerString);

                try
                {
                    //Überprüfen ob die Aufnahmequelle eine Liste mit möglichen Aufnahme-
                    //Auflösungen mitliefert.
                    if (videoSource.VideoCapabilities.Length > 0)
                    {
                        //Das Profil mit der höchsten Auflösung suchen
                        for (int i = 0; i < videoSource.VideoCapabilities.Length; i++)
                        {
                            if (videoSource.VideoCapabilities[i].FrameSize.Width > Convert.ToInt32(highestSolution.Split(';')[0]))
                                highestSolution = videoSource.VideoCapabilities[i].FrameSize.Width.ToString() + ";" + videoSource.VideoCapabilities[i].FrameSize.Height.ToString()/*i.ToString()*/;
                        }
                        //Dem Webcam Objekt ermittelte Auflösung übergeben
                        videoSource.DesiredFrameSize = videoSource.VideoCapabilities[Convert.ToInt32(highestSolution.Split(';')[1])].FrameSize;
                    }
                }
                catch { }
                oldPic = (Bitmap)emptyPic.Clone();
                
                //http Post
                httpPost = new HttpPost("Beispiel Url");                                //TODO: URL

                //NewFrame Eventhandler zuweisen anlegen.
                //(Dieser registriert jeden neuen Frame der Webcam)
                videoSource.NewFrame += new AForge.Video.NewFrameEventHandler(videoSource_NewFrame);

                //Das Aufnahmegerät aktivieren
                videoSource.Start();
            }
        }

        void videoSource_NewFrame(object sender, AForge.Video.NewFrameEventArgs eventArgs)
        {
            Bitmap newPic = (Bitmap)eventArgs.Frame.Clone();
            if (show)
            {
                //Jedes ankommende Objekt als Bitmap casten und der Picturebox zuweisen
                //(Denkt an das ".Clone()", um Zugriffsverletzungen aus dem Weg zu gehen.)
                pictureBoxVideo.BackgroundImage = (Bitmap)eventArgs.Frame.Clone();

                Bitmap image = (Bitmap)emptyPic.Clone();

                int iMax = Convert.ToInt32(highestSolution.Split(';')[0]);  //max Breite
                int jMax = Convert.ToInt32(highestSolution.Split(';')[1]);  //max Höhe

                //Alle Zeilen und Spalten durchgehen
                for (int i = 0; i < iMax; i +=4)
                {
                    for (int j = 0; j < jMax; j += 5)
                    {
                        //differenz des Blauanteils von Bild mit vorhergegangenem Bild
                        byte b = (byte)(newPic.GetPixel(i, j).B - oldPic.GetPixel(i, j).B);

                        //Wenn b > 40 oder b < -40. Bei der Byte Substraktion kommt es bei negativem b zum übertrag
                        //215 = 255 - 40
                        //Das NICHT ist wegen Laufzeitoptimierung. ODER geht schneller als UND
                        if (!(b < 40 || b > 215))
                        {
                            image.SetPixel(i, j, Color.Black);
                        }
                    }
                }
                //PostParamCollection postParamCollection = new PostParamCollection();
                //postParamCollection.Add(new PostParam("Time", DateTime.Now.ToString("HH:mm")));                    //TODO: Parameter
                //string sendImage = image.ToString();
                //postParamCollection.Add(new PostParam("Image", sendImage));
                //httpPost.doPost(postParamCollection);
                pictureBoxChange.Image = image;
                oldPic = (Bitmap)newPic.Clone();
            }
            show = !show;
            
        }

        private void Form1_FormClosed(object sender, FormClosedEventArgs e)
        {
            //Beim Beenden des Programmes die Webcam wieder "freigeben",
            //damit sie von anderen Anwendungen benutzt werden kann.
            if (videoSource != null && videoSource.IsRunning)
            {
                videoSource.SignalToStop();
                videoSource = null;
            }
        }

        
    }

    class HttpPost
    {
        public HttpPost(string postUri)
        {
            this.PostUri = postUri;
        }

        private WebProxy _proxy;
        public WebProxy Proxy
        {
            get { return _proxy; }
            set { _proxy = value; }
        }

        private string _postUri;
        public string PostUri
        {
            get { return _postUri; }
            set { _postUri = value; }
        }

        private string _language;
        public string Language
        {
            get { return _language; }
            set { _language = value; }
        }

        public event EventHandler PostComplete;

        public void doPost(PostParamCollection postParamCollection)
        {
            try
            {
                Uri uri = new Uri(this.PostUri);
                WebRequest webRequest = WebRequest.Create(uri);
                webRequest.Headers.Add(HttpRequestHeader.AcceptLanguage, this.Language);

                if (this.Proxy != null)
                    webRequest.Proxy = Proxy;

                webRequest.Method = "POST";
                webRequest.ContentType = "application/x-www-form-urlencoded";

                string parameterString = "";

                foreach (PostParam parameter in postParamCollection)
                {
                    parameterString += parameter.Paramter + "=" + parameter.Value + "&";
                }

                byte[] byteArray = Encoding.UTF8.GetBytes(parameterString);
                webRequest.ContentLength = byteArray.Length;

                Stream stream = webRequest.GetRequestStream();
                stream.Write(byteArray, 0, byteArray.Length);
                stream.Close();

                WebResponse webResponse = webRequest.GetResponse();
                stream = webResponse.GetResponseStream();

                StreamReader streamReader = new StreamReader(stream);
                string responseStream = streamReader.ReadToEnd();

                webResponse.Close();
                streamReader.Close();

                if (PostComplete != null)
                {
                    PostComplete.Invoke(responseStream, null);
                }
            }
            catch (Exception exception)
            {
                throw exception;
            }
        }
    }
    class PostParam
    {
        public PostParam() { }

        public PostParam(string paramter, string value)
        {
            this.Paramter = paramter;
            this.Value = value;
        }

        private string _paramter;
        public string Paramter
        {
            get { return _paramter; }
            set { _paramter = value; }
        }

        private string _value;
        public string Value
        {
            get { return _value; ; }
            set { _value = value; }
        }
    }

    class PostParamCollection : List<PostParam>
    {
    }

}

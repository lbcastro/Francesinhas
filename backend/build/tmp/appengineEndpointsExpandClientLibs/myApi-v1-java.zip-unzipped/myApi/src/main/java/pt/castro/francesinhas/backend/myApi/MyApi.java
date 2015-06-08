/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-03-26 20:30:19 UTC)
 * on 2015-06-08 at 00:53:50 UTC 
 * Modify at your own risk.
 */

package pt.castro.francesinhas.backend.myApi;

/**
 * Service definition for MyApi (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link MyApiRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class MyApi extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.20.0 of the myApi library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://castro-francesinhas.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "myApi/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public MyApi(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  MyApi(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "addItem".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link AddItem#execute()} method to invoke the remote operation.
   *
   * @param content the {@link pt.castro.francesinhas.backend.myApi.model.ItemHolder}
   * @return the request
   */
  public AddItem addItem(pt.castro.francesinhas.backend.myApi.model.ItemHolder content) throws java.io.IOException {
    AddItem result = new AddItem(content);
    initialize(result);
    return result;
  }

  public class AddItem extends MyApiRequest<pt.castro.francesinhas.backend.myApi.model.ItemHolder> {

    private static final String REST_PATH = "addItem";

    /**
     * Create a request for the method "addItem".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link AddItem#execute()} method to invoke the remote operation. <p>
     * {@link
     * AddItem#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link pt.castro.francesinhas.backend.myApi.model.ItemHolder}
     * @since 1.13
     */
    protected AddItem(pt.castro.francesinhas.backend.myApi.model.ItemHolder content) {
      super(MyApi.this, "POST", REST_PATH, content, pt.castro.francesinhas.backend.myApi.model.ItemHolder.class);
    }

    @Override
    public AddItem setAlt(java.lang.String alt) {
      return (AddItem) super.setAlt(alt);
    }

    @Override
    public AddItem setFields(java.lang.String fields) {
      return (AddItem) super.setFields(fields);
    }

    @Override
    public AddItem setKey(java.lang.String key) {
      return (AddItem) super.setKey(key);
    }

    @Override
    public AddItem setOauthToken(java.lang.String oauthToken) {
      return (AddItem) super.setOauthToken(oauthToken);
    }

    @Override
    public AddItem setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (AddItem) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public AddItem setQuotaUser(java.lang.String quotaUser) {
      return (AddItem) super.setQuotaUser(quotaUser);
    }

    @Override
    public AddItem setUserIp(java.lang.String userIp) {
      return (AddItem) super.setUserIp(userIp);
    }

    @Override
    public AddItem set(String parameterName, Object value) {
      return (AddItem) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "addUser".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link AddUser#execute()} method to invoke the remote operation.
   *
   * @param content the {@link pt.castro.francesinhas.backend.myApi.model.UserHolder}
   * @return the request
   */
  public AddUser addUser(pt.castro.francesinhas.backend.myApi.model.UserHolder content) throws java.io.IOException {
    AddUser result = new AddUser(content);
    initialize(result);
    return result;
  }

  public class AddUser extends MyApiRequest<pt.castro.francesinhas.backend.myApi.model.UserHolder> {

    private static final String REST_PATH = "addUser";

    /**
     * Create a request for the method "addUser".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link AddUser#execute()} method to invoke the remote operation. <p>
     * {@link
     * AddUser#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link pt.castro.francesinhas.backend.myApi.model.UserHolder}
     * @since 1.13
     */
    protected AddUser(pt.castro.francesinhas.backend.myApi.model.UserHolder content) {
      super(MyApi.this, "POST", REST_PATH, content, pt.castro.francesinhas.backend.myApi.model.UserHolder.class);
    }

    @Override
    public AddUser setAlt(java.lang.String alt) {
      return (AddUser) super.setAlt(alt);
    }

    @Override
    public AddUser setFields(java.lang.String fields) {
      return (AddUser) super.setFields(fields);
    }

    @Override
    public AddUser setKey(java.lang.String key) {
      return (AddUser) super.setKey(key);
    }

    @Override
    public AddUser setOauthToken(java.lang.String oauthToken) {
      return (AddUser) super.setOauthToken(oauthToken);
    }

    @Override
    public AddUser setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (AddUser) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public AddUser setQuotaUser(java.lang.String quotaUser) {
      return (AddUser) super.setQuotaUser(quotaUser);
    }

    @Override
    public AddUser setUserIp(java.lang.String userIp) {
      return (AddUser) super.setUserIp(userIp);
    }

    @Override
    public AddUser set(String parameterName, Object value) {
      return (AddUser) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "addUserVote".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link AddUserVote#execute()} method to invoke the remote operation.
   *
   * @param itemId
   * @param vote
   * @param content the {@link pt.castro.francesinhas.backend.myApi.model.UserHolder}
   * @return the request
   */
  public AddUserVote addUserVote(java.lang.String itemId, java.lang.Integer vote, pt.castro.francesinhas.backend.myApi.model.UserHolder content) throws java.io.IOException {
    AddUserVote result = new AddUserVote(itemId, vote, content);
    initialize(result);
    return result;
  }

  public class AddUserVote extends MyApiRequest<pt.castro.francesinhas.backend.myApi.model.UserHolder> {

    private static final String REST_PATH = "addUserVote/{itemId}/{vote}";

    /**
     * Create a request for the method "addUserVote".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link AddUserVote#execute()} method to invoke the remote operation. <p>
     * {@link
     * AddUserVote#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param itemId
     * @param vote
     * @param content the {@link pt.castro.francesinhas.backend.myApi.model.UserHolder}
     * @since 1.13
     */
    protected AddUserVote(java.lang.String itemId, java.lang.Integer vote, pt.castro.francesinhas.backend.myApi.model.UserHolder content) {
      super(MyApi.this, "POST", REST_PATH, content, pt.castro.francesinhas.backend.myApi.model.UserHolder.class);
      this.itemId = com.google.api.client.util.Preconditions.checkNotNull(itemId, "Required parameter itemId must be specified.");
      this.vote = com.google.api.client.util.Preconditions.checkNotNull(vote, "Required parameter vote must be specified.");
    }

    @Override
    public AddUserVote setAlt(java.lang.String alt) {
      return (AddUserVote) super.setAlt(alt);
    }

    @Override
    public AddUserVote setFields(java.lang.String fields) {
      return (AddUserVote) super.setFields(fields);
    }

    @Override
    public AddUserVote setKey(java.lang.String key) {
      return (AddUserVote) super.setKey(key);
    }

    @Override
    public AddUserVote setOauthToken(java.lang.String oauthToken) {
      return (AddUserVote) super.setOauthToken(oauthToken);
    }

    @Override
    public AddUserVote setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (AddUserVote) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public AddUserVote setQuotaUser(java.lang.String quotaUser) {
      return (AddUserVote) super.setQuotaUser(quotaUser);
    }

    @Override
    public AddUserVote setUserIp(java.lang.String userIp) {
      return (AddUserVote) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String itemId;

    /**

     */
    public java.lang.String getItemId() {
      return itemId;
    }

    public AddUserVote setItemId(java.lang.String itemId) {
      this.itemId = itemId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Integer vote;

    /**

     */
    public java.lang.Integer getVote() {
      return vote;
    }

    public AddUserVote setVote(java.lang.Integer vote) {
      this.vote = vote;
      return this;
    }

    @Override
    public AddUserVote set(String parameterName, Object value) {
      return (AddUserVote) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "decreaseScore".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link DecreaseScore#execute()} method to invoke the remote operation.
   *
   * @param content the {@link pt.castro.francesinhas.backend.myApi.model.ItemHolder}
   * @return the request
   */
  public DecreaseScore decreaseScore(pt.castro.francesinhas.backend.myApi.model.ItemHolder content) throws java.io.IOException {
    DecreaseScore result = new DecreaseScore(content);
    initialize(result);
    return result;
  }

  public class DecreaseScore extends MyApiRequest<pt.castro.francesinhas.backend.myApi.model.ItemHolder> {

    private static final String REST_PATH = "decreaseScore";

    /**
     * Create a request for the method "decreaseScore".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link DecreaseScore#execute()} method to invoke the remote operation. <p>
     * {@link DecreaseScore#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientR
     * equest)} must be called to initialize this instance immediately after invoking the constructor.
     * </p>
     *
     * @param content the {@link pt.castro.francesinhas.backend.myApi.model.ItemHolder}
     * @since 1.13
     */
    protected DecreaseScore(pt.castro.francesinhas.backend.myApi.model.ItemHolder content) {
      super(MyApi.this, "POST", REST_PATH, content, pt.castro.francesinhas.backend.myApi.model.ItemHolder.class);
    }

    @Override
    public DecreaseScore setAlt(java.lang.String alt) {
      return (DecreaseScore) super.setAlt(alt);
    }

    @Override
    public DecreaseScore setFields(java.lang.String fields) {
      return (DecreaseScore) super.setFields(fields);
    }

    @Override
    public DecreaseScore setKey(java.lang.String key) {
      return (DecreaseScore) super.setKey(key);
    }

    @Override
    public DecreaseScore setOauthToken(java.lang.String oauthToken) {
      return (DecreaseScore) super.setOauthToken(oauthToken);
    }

    @Override
    public DecreaseScore setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (DecreaseScore) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public DecreaseScore setQuotaUser(java.lang.String quotaUser) {
      return (DecreaseScore) super.setQuotaUser(quotaUser);
    }

    @Override
    public DecreaseScore setUserIp(java.lang.String userIp) {
      return (DecreaseScore) super.setUserIp(userIp);
    }

    @Override
    public DecreaseScore set(String parameterName, Object value) {
      return (DecreaseScore) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getUser".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link GetUser#execute()} method to invoke the remote operation.
   *
   * @param userToken
   * @return the request
   */
  public GetUser getUser(java.lang.String userToken) throws java.io.IOException {
    GetUser result = new GetUser(userToken);
    initialize(result);
    return result;
  }

  public class GetUser extends MyApiRequest<pt.castro.francesinhas.backend.myApi.model.UserHolder> {

    private static final String REST_PATH = "userholder/{userToken}";

    /**
     * Create a request for the method "getUser".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link GetUser#execute()} method to invoke the remote operation. <p>
     * {@link
     * GetUser#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param userToken
     * @since 1.13
     */
    protected GetUser(java.lang.String userToken) {
      super(MyApi.this, "GET", REST_PATH, null, pt.castro.francesinhas.backend.myApi.model.UserHolder.class);
      this.userToken = com.google.api.client.util.Preconditions.checkNotNull(userToken, "Required parameter userToken must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetUser setAlt(java.lang.String alt) {
      return (GetUser) super.setAlt(alt);
    }

    @Override
    public GetUser setFields(java.lang.String fields) {
      return (GetUser) super.setFields(fields);
    }

    @Override
    public GetUser setKey(java.lang.String key) {
      return (GetUser) super.setKey(key);
    }

    @Override
    public GetUser setOauthToken(java.lang.String oauthToken) {
      return (GetUser) super.setOauthToken(oauthToken);
    }

    @Override
    public GetUser setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetUser) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetUser setQuotaUser(java.lang.String quotaUser) {
      return (GetUser) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetUser setUserIp(java.lang.String userIp) {
      return (GetUser) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String userToken;

    /**

     */
    public java.lang.String getUserToken() {
      return userToken;
    }

    public GetUser setUserToken(java.lang.String userToken) {
      this.userToken = userToken;
      return this;
    }

    @Override
    public GetUser set(String parameterName, Object value) {
      return (GetUser) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "increaseScore".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link IncreaseScore#execute()} method to invoke the remote operation.
   *
   * @param content the {@link pt.castro.francesinhas.backend.myApi.model.ItemHolder}
   * @return the request
   */
  public IncreaseScore increaseScore(pt.castro.francesinhas.backend.myApi.model.ItemHolder content) throws java.io.IOException {
    IncreaseScore result = new IncreaseScore(content);
    initialize(result);
    return result;
  }

  public class IncreaseScore extends MyApiRequest<pt.castro.francesinhas.backend.myApi.model.ItemHolder> {

    private static final String REST_PATH = "increaseScore";

    /**
     * Create a request for the method "increaseScore".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link IncreaseScore#execute()} method to invoke the remote operation. <p>
     * {@link IncreaseScore#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientR
     * equest)} must be called to initialize this instance immediately after invoking the constructor.
     * </p>
     *
     * @param content the {@link pt.castro.francesinhas.backend.myApi.model.ItemHolder}
     * @since 1.13
     */
    protected IncreaseScore(pt.castro.francesinhas.backend.myApi.model.ItemHolder content) {
      super(MyApi.this, "POST", REST_PATH, content, pt.castro.francesinhas.backend.myApi.model.ItemHolder.class);
    }

    @Override
    public IncreaseScore setAlt(java.lang.String alt) {
      return (IncreaseScore) super.setAlt(alt);
    }

    @Override
    public IncreaseScore setFields(java.lang.String fields) {
      return (IncreaseScore) super.setFields(fields);
    }

    @Override
    public IncreaseScore setKey(java.lang.String key) {
      return (IncreaseScore) super.setKey(key);
    }

    @Override
    public IncreaseScore setOauthToken(java.lang.String oauthToken) {
      return (IncreaseScore) super.setOauthToken(oauthToken);
    }

    @Override
    public IncreaseScore setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (IncreaseScore) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public IncreaseScore setQuotaUser(java.lang.String quotaUser) {
      return (IncreaseScore) super.setQuotaUser(quotaUser);
    }

    @Override
    public IncreaseScore setUserIp(java.lang.String userIp) {
      return (IncreaseScore) super.setUserIp(userIp);
    }

    @Override
    public IncreaseScore set(String parameterName, Object value) {
      return (IncreaseScore) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listItems".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link ListItems#execute()} method to invoke the remote operation.
   *
   * @return the request
   */
  public ListItems listItems() throws java.io.IOException {
    ListItems result = new ListItems();
    initialize(result);
    return result;
  }

  public class ListItems extends MyApiRequest<pt.castro.francesinhas.backend.myApi.model.CollectionResponseItemHolder> {

    private static final String REST_PATH = "itemholder";

    /**
     * Create a request for the method "listItems".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link ListItems#execute()} method to invoke the remote operation. <p>
     * {@link
     * ListItems#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected ListItems() {
      super(MyApi.this, "GET", REST_PATH, null, pt.castro.francesinhas.backend.myApi.model.CollectionResponseItemHolder.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ListItems setAlt(java.lang.String alt) {
      return (ListItems) super.setAlt(alt);
    }

    @Override
    public ListItems setFields(java.lang.String fields) {
      return (ListItems) super.setFields(fields);
    }

    @Override
    public ListItems setKey(java.lang.String key) {
      return (ListItems) super.setKey(key);
    }

    @Override
    public ListItems setOauthToken(java.lang.String oauthToken) {
      return (ListItems) super.setOauthToken(oauthToken);
    }

    @Override
    public ListItems setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListItems) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListItems setQuotaUser(java.lang.String quotaUser) {
      return (ListItems) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListItems setUserIp(java.lang.String userIp) {
      return (ListItems) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Integer count;

    /**

     */
    public java.lang.Integer getCount() {
      return count;
    }

    public ListItems setCount(java.lang.Integer count) {
      this.count = count;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListItems setCursor(java.lang.String cursor) {
      this.cursor = cursor;
      return this;
    }

    @Override
    public ListItems set(String parameterName, Object value) {
      return (ListItems) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateItem".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link UpdateItem#execute()} method to invoke the remote operation.
   *
   * @param content the {@link pt.castro.francesinhas.backend.myApi.model.ItemHolder}
   * @return the request
   */
  public UpdateItem updateItem(pt.castro.francesinhas.backend.myApi.model.ItemHolder content) throws java.io.IOException {
    UpdateItem result = new UpdateItem(content);
    initialize(result);
    return result;
  }

  public class UpdateItem extends MyApiRequest<pt.castro.francesinhas.backend.myApi.model.ItemHolder> {

    private static final String REST_PATH = "itemholder";

    /**
     * Create a request for the method "updateItem".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link UpdateItem#execute()} method to invoke the remote operation. <p>
     * {@link
     * UpdateItem#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link pt.castro.francesinhas.backend.myApi.model.ItemHolder}
     * @since 1.13
     */
    protected UpdateItem(pt.castro.francesinhas.backend.myApi.model.ItemHolder content) {
      super(MyApi.this, "PUT", REST_PATH, content, pt.castro.francesinhas.backend.myApi.model.ItemHolder.class);
    }

    @Override
    public UpdateItem setAlt(java.lang.String alt) {
      return (UpdateItem) super.setAlt(alt);
    }

    @Override
    public UpdateItem setFields(java.lang.String fields) {
      return (UpdateItem) super.setFields(fields);
    }

    @Override
    public UpdateItem setKey(java.lang.String key) {
      return (UpdateItem) super.setKey(key);
    }

    @Override
    public UpdateItem setOauthToken(java.lang.String oauthToken) {
      return (UpdateItem) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateItem setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateItem) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateItem setQuotaUser(java.lang.String quotaUser) {
      return (UpdateItem) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateItem setUserIp(java.lang.String userIp) {
      return (UpdateItem) super.setUserIp(userIp);
    }

    @Override
    public UpdateItem set(String parameterName, Object value) {
      return (UpdateItem) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link MyApi}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link MyApi}. */
    @Override
    public MyApi build() {
      return new MyApi(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link MyApiRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setMyApiRequestInitializer(
        MyApiRequestInitializer myapiRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(myapiRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}

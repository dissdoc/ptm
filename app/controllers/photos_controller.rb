class PhotosController < ApplicationController
  before_filter :authenticate_user!, :except => [:show]
  before_filter :set_album, :except => [:index]
  before_filter :set_photo, :except => [:new, :create, :destroy, :index]
  before_filter :album_admin?, :only => [:new, :create, :destroy, :edit, :update, :apply_recommend, :destroy_recommend,
    :apply_recommend_at, :destroy_recommend_at]
  before_filter :not_admin_photo?, :only => [:recommend_geo, :create_recommend, :recommend_at, :create_recommend_at]
  before_filter :can_delete?, :only => [:deletearea]
  before_filter :set_group, :only => [:agree_link_photo, :cancel_link_photo]

  def index
    @photos = current_user.photos
  end

  def new
    @photo = @album.photos.new
    @photo.build_geo
    @photo.build_share_photo
  end

  def create
    @photo = @album.photos.new(params[:photo])
    @photo.user_id = current_user.id
    if @photo.save!
      Activity.create!(:user_id => current_user.id, :action => 'add', :object_name => 'photo', :object_link => album_photo_path(@album, @photo))
      redirect_to @album
    else
      redirect_to root_path
    end
  end

  def show
    @notes = @photo.notes.all
    @note = @photo.notes.new
  end

  def edit
    @photo.geo ? @photo.geo : @photo.build_geo
    @photo.share_photo ? @photo.share_photo : @photo.build_share_photo
  end

  def update
    if @photo.update_attributes(params[:photo])
      redirect_to [@album, @photo]
    else
      render :action => "edit"
    end
  end

  def destroy
    @photo = current_user.photos.find(params[:id])
    @album = @photo.album
    @photo.destroy
    redirect_to @album
  end

  def add_note
    current_notes = @photo.notes.all.uniq{ |note| note.user_id }

    @note = @photo.notes.new(params[:note])
    @note.user_id = current_user.id
    if @note.save!
      UserMailer.commented_photo(current_user, @photo).deliver
      UserMailer.also_added_comment(current_user, @photo, current_notes).deliver if current_notes.count > 0
      redirect_to [@album, @photo]
    else
      redirect_to root_path
    end
  end

  def recommend_geo
    @geo = @photo.geo.present? ? @photo.geo : @geo = params[:address]
    @lat = @photo.geo.present? ? @photo.geo.latitude : '-34.397'
    @lng = @photo.geo.present? ? @photo.geo.longitude : '150.644'
  end

  def create_recommend
    @recommend = @photo.recommend_geos.new

    @recommend.address = params[:address]
    @recommend.comment = params[:comment]
    @recommend.user = current_user
    if @recommend.save!
      UserMailer.suggested_geotag(current_user, @photo).deliver
      @message = Message.new
      @message.theme = "Recommended geotag..."
      @message.description = "User #{current_user.full_name} recommended new geolocation for photo #{@photo}"
      @message.to_user = @photo.user
      @message.from_user = current_user
      @message.save!

      redirect_to album_photo_path(@album, @photo)
    else
      render :action => :recommend_geo
    end
  end

  def apply_recommend
    recommend = RecommendGeo.find(params[:recommend_id])

    if @photo.geo.present?
      @photo.geo.update_attributes(:address => recommend.address, :latitude => recommend.latitude,
                                    :longitude => recommend.longitude)
    else
      @geo = @photo.build_geo(:address => recommend.address, :latitude => recommend.latitude,
                       :longitude => recommend.longitude)
      @geo.save!
    end
    recommend.destroy
    redirect_to album_photo_path(@album, @photo)
  end

  def destroy_recommend
    recommend = RecommendGeo.find(params[:recommend_id])
    recommend.destroy
    redirect_to album_photo_path(@album, @photo)
  end

  # Recommend generated at ---------------------------------------------------------------------------------------------
  def recommend_at

  end

  def create_recommend_at
    @recommend = @photo.recommend_ats.new

    @recommend.from_at = params[:from_at].to_s
    @recommend.to_at = params[:to_at].to_s
    @recommend.user = current_user
    if @recommend.save!
      UserMailer.suggested_generate(current_user, @photo).deliver
      @message = Message.new
      @message.theme = "Recommended time tag..."
      @message.description = "User #{current_user.full_name} recommended new generate date for photo #{@photo}"
      @message.to_user = @photo.user
      @message.from_user = current_user
      @message.save!
      redirect_to album_photo_path(@album, @photo)
    else
      render :action => :recommend_at
    end
  end

  def apply_recommend_at
    recommend = RecommendAt.find(params[:recommend_id])
    @photo.update_attributes(:generate => recommend.from_at, :generate_end => recommend.to_at)
    recommend.destroy
    redirect_to album_photo_path(@album, @photo)
  end

  def destroy_recommend_at
    recommend = RecommendAt.find(params[:recommend_id])
    recommend.destroy
    redirect_to album_photo_path(@album, @photo)
  end
  # end of recommend generated at --------------------------------------------------------------------------------------

  def selected

  end

  def addarea
    if params[:description].blank?
      render :action => :selected
    else
      @note = @photo.areatags.create!(:x => params[:x1], :y => params[:y1], :height => params[:height], :width => params[:width],
        :description => params[:description], :user_id => current_user.id)
      UserMailer.added_note(current_user, @note, @photo).deliver
      redirect_to selected_album_photo_path(@album, @photo)
    end
  end

  def deletearea
    area = @photo.areatags.find(params[:area])
    area.destroy
    redirect_to selected_album_photo_path(@album, @photo)
  end

  def agree_link_photo
    @group.photo_group_joins.where(:photo_id => @photo.id).first.accept!
    redirect_to [@album, @photo]
  end

  def cancel_link_photo
    @group.photo_group_joins.where(:photo_id => @photo.id).first.destroy
    redirect_to [@album, @photo]
  end

  protected
    def set_photo
      @photo = @album.photos.find(params[:id])
    end

    def set_album
      @album = Album.find(params[:album_id])
    end

    def album_admin?
      redirect_to root_path if current_user.id != @album.user.id
    end

    def not_admin_photo?
      redirect_to @photo if current_user.id == @photo.user.id
    end

    def can_delete?
      @areatag = Areatag.find(params[:area])

      !current_user.admin_of_photo?(@photo) && !current_user.admin_of_areatag?(@photo, @areatag)
    end

    def set_group
      @group = Group.find(params[:group_id])
    end
end
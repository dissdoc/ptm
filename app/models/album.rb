class Album < ActiveRecord::Base
  belongs_to :user

  has_many :photos, :dependent => :destroy
  has_many :photo_album_joins, :dependent => :destroy

  validates :name, :user_id, :presence => true

  def show_thumbnail
    photos.where("id = ?", thumbnail).first
  end
end

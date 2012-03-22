class Tagging < ActiveRecord::Base
  belongs_to :photo
  belongs_to :tag

  def self.get_by_tag(tag)
    where('tag_id = ?', tag.id)
  end
end
